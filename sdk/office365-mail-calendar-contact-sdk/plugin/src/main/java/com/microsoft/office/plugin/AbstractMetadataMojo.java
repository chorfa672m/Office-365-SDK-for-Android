/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.microsoft.office.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public abstract class AbstractMetadataMojo extends AbstractMojo {

    /**
     * Generated files base root.
     */
    @Parameter(property = "outputDirectory", required = true)
    protected String outputDirectory;

    /**
     * OData service root URL.
     */
    @Parameter(property = "serviceRootURL", required = true)
    protected String serviceRootURL;

    /**
     * Base package.
     */
    @Parameter(property = "basePackage", required = true)
    protected String basePackage;

    protected final Set<String> namespaces = new HashSet<String>();

    protected static String TOOL_DIR = "ojc-plugin";

    protected AbstractUtility utility;

    protected abstract AbstractUtility getUtility();

    protected abstract String getVersion();

    protected File mkdir(final String path) {
        final File dir = new File(outputDirectory + File.separator + TOOL_DIR + File.separator + path);

        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new IllegalArgumentException("Invalid path '" + path + "': it is not a directory");
            }
        } else {
            dir.mkdirs();
        }

        return dir;
    }

    protected File mkPkgDir(final String path) {
        return mkdir(basePackage.replace('.', File.separatorChar) + File.separator + path);
    }

    protected void writeFile(final String name, final File path, final VelocityContext ctx, final Template template,
            final boolean append) throws MojoExecutionException {

        if (!path.exists()) {
            throw new IllegalArgumentException("Invalid base path '" + path.getAbsolutePath() + "'");
        }

        FileWriter writer = null;
        try {
            final File toBeWritten = new File(path, name);
            if (!append && toBeWritten.exists()) {
               throw new IllegalStateException("File '" + toBeWritten.getAbsolutePath() + "' already exists");
            }
            writer = new FileWriter(toBeWritten, append);
            template.merge(ctx, writer);
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file '" + name + "'", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    protected VelocityContext newContext() {

        final VelocityContext ctx = new VelocityContext();

        ctx.put("utility", getUtility());
        ctx.put("basePackage", basePackage);
        ctx.put("schemaName", getUtility().getSchemaName());
        ctx.put("namespace", getUtility().getNamespace());
        ctx.put("namespaces", namespaces);
        ctx.put("odataVersion", getVersion());

        return ctx;
    }

    protected void parseObj(final File base, final String pkg, final String name, final String out)
            throws MojoExecutionException {

        parseObj(base, false, pkg, name, out, Collections.<String, Object>emptyMap());
    }

    protected void parseObj(
            final File base,
            final String pkg,
            final String name,
            final String out,
            final Map<String, Object> objs)
            throws MojoExecutionException {

        parseObj(base, false, pkg, name, out, objs);
    }

    protected void parseObj(
            final File base,
            final boolean append,
            final String pkg,
            final String name,
            final String out,
            final Map<String, Object> objs)
            throws MojoExecutionException {

        final VelocityContext ctx = newContext();
        ctx.put("package", pkg);

        if (objs != null) {
            for (Map.Entry<String, Object> obj : objs.entrySet()) {
                if (StringUtils.isNotBlank(obj.getKey()) && obj.getValue() != null) {
                    ctx.put(obj.getKey(), obj.getValue());
                }
            }
        }

        final Template template = Velocity.getTemplate(name + ".vm");
        writeFile(out, base, ctx, template, append);
    }
}
