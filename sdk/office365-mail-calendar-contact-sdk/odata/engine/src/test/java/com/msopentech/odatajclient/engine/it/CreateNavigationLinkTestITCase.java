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
package com.msopentech.odatajclient.engine.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.msopentech.odatajclient.engine.client.http.HttpClientException;
import com.msopentech.odatajclient.engine.communication.request.cud.CUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataDeleteRequest;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntitySetRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.RetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataDeleteResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataObjectFactory;
import com.msopentech.odatajclient.engine.data.ODataInlineEntity;
import com.msopentech.odatajclient.engine.data.ODataInlineEntitySet;
import com.msopentech.odatajclient.engine.data.ODataLink;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.utils.URIUtils;

public class CreateNavigationLinkTestITCase extends AbstractTest {

    // create navigation link with ATOM
    @Test
    public void createNavWithAtom() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final String contentType = "application/atom+xml";
        final String prefer = "return-content";
        final ODataEntity actual = createNavigation(format, 20, contentType, prefer);
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // create navigation link with JSON full metadata

    @Test
    public void createNavWithJSONFullMetadata() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contentType = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        final ODataEntity actual = createNavigation(format, 21, contentType, prefer);
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // throws Null pointer exception when the format is JSON No metadata

    @Test(expected = HttpClientException.class)
    public void createNavWithJSONNoMetadata() {
        final ODataPubFormat format = ODataPubFormat.JSON_NO_METADATA;
        final String contentType = "application/json;odata=nometadata";
        final String prefer = "return-content";
        final ODataEntity actual = createNavigation(format, 22, contentType, prefer);
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // test with JSON accept and atom content type

    @Test
    public void createNavWithJSONAndATOM() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contentType = "application/atom+xml";
        final String prefer = "return-content";
        final ODataEntity actual = createNavigation(format, 23, contentType, prefer);
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // test with JSON full metadata in format and json no metadata in content type

    @Test
    public void createNavWithDiffJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contentType = "application/json;odata=nometadata";
        final String prefer = "return-content";
        final ODataEntity actual = createNavigation(format, 24, contentType, prefer);
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // test with JSON no metadata format and json no metadata in content type

    @Test(expected = HttpClientException.class)
    public void createNavWithNoMetadata() {
        final ODataPubFormat format = ODataPubFormat.JSON_NO_METADATA;
        final String contentType = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        final ODataEntity actual = createNavigation(format, 25, contentType, prefer);
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // create collection navigation link with ATOM

    @Test
    public void createCollectionNavWithAtom() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final String contentType = "application/atom+xml";
        final String prefer = "return-content";
        final ODataEntity actual = createCollectionNavigation(format, 55, contentType, prefer);
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // create collection navigation link with JSON

    @Test
    public void createCollectionNavWithJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contentType = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        final ODataEntity actual = createCollectionNavigation(format, 77, contentType, prefer);
        delete(format, actual, false, testDefaultServiceRootURL);
    }

    // create a navigation link
    public ODataEntity createNavigation(final ODataPubFormat format, final int id, final String contenttype,
            final String prefer) {
        final String name = "Customer Navigation test";

        final ODataEntity original = getNewCustomer(id, name, false);
        original.addLink(ODataObjectFactory.newEntityNavigationLink(
                "Info", URI.create(testDefaultServiceRootURL + "/CustomerInfo(11)")));
        final ODataEntity created = createNav(testDefaultServiceRootURL, format, original, "Customer", contenttype,
                prefer);

        final ODataEntity actual = validateEntities(testDefaultServiceRootURL, format, created, id, null, "Customer");

        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL);
        uriBuilder.appendEntityTypeSegment("Customer").appendKeySegment(id).appendEntityTypeSegment("Info");

        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(format);
        req.setContentType(contenttype);
        req.setPrefer(prefer);
        final ODataRetrieveResponse<ODataEntity> res = req.execute();
        assertEquals(200, res.getStatusCode());
        assertTrue(res.getHeader("DataServiceVersion").contains("3.0;"));
        final ODataEntity entity = res.getBody();
        assertNotNull(entity);
        for (ODataProperty prop : entity.getProperties()) {
            if ("CustomerInfoId".equals(prop.getName())) {
                assertEquals("11", prop.getValue().toString());
            }
        }
        return actual;
    }

    // create a navigation link
    public ODataEntity createNav(final String url, final ODataPubFormat format, final ODataEntity original,
            final String entitySetName, final String contentType, final String prefer) {
        final URIBuilder uriBuilder = client.getURIBuilder(url);
        uriBuilder.appendEntitySetSegment(entitySetName);
        final ODataEntityCreateRequest createReq =
                client.getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), original);
        createReq.setFormat(format);
        createReq.setContentType(contentType);
        createReq.setPrefer(prefer);
        final ODataEntityCreateResponse createRes = createReq.execute();
        assertEquals(201, createRes.getStatusCode());

        assertEquals("Created", createRes.getStatusMessage());

        final ODataEntity created = createRes.getBody();
        assertNotNull(created);
        return created;
    }
    // create collection navigation link

    public ODataEntity createCollectionNavigation(final ODataPubFormat format, final int id,
            final String contentType, final String prefer) {
        {
            final String name = "Collection Navigation Key Customer";
            final ODataEntity original = getNewCustomer(id, name, false);

            final Set<Integer> navigationKeys = new HashSet<Integer>();
            navigationKeys.add(-118);
            navigationKeys.add(-119);

            for (Integer key : navigationKeys) {
                final ODataEntity orderEntity =
                        ODataObjectFactory.newEntity("Microsoft.Test.OData.Services.AstoriaDefaultService.Order");

                orderEntity.addProperty(ODataObjectFactory.newPrimitiveProperty("OrderId",
                        client.getPrimitiveValueBuilder().setValue(key).setType(EdmSimpleType.Int32).build()));
                orderEntity.addProperty(ODataObjectFactory.newPrimitiveProperty("CustomerId",
                        client.getPrimitiveValueBuilder().setValue(id).setType(EdmSimpleType.Int32).build()));

                final ODataEntityCreateRequest createReq = client.getCUDRequestFactory().getEntityCreateRequest(
                        client.getURIBuilder(testDefaultServiceRootURL).appendEntitySetSegment("Order").build(),
                        orderEntity);
                createReq.setFormat(format);
                createReq.setContentType(contentType);
                original.addLink(ODataObjectFactory.newFeedNavigationLink(
                        "Orders",
                        createReq.execute().getBody().getEditLink()));
            }
            final ODataEntity createdEntity = createNav(testDefaultServiceRootURL, format, original, "Customer",
                    contentType, prefer);
            final ODataEntity actualEntity =
                    validateEntities(testDefaultServiceRootURL, format, createdEntity, id, null, "Customer");

            final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL);
            uriBuilder.appendEntityTypeSegment("Customer").appendKeySegment(id).appendEntityTypeSegment("Orders");

            final ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
            req.setFormat(format);

            final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
            assertEquals(200, res.getStatusCode());

            final ODataEntitySet entitySet = res.getBody();
            assertNotNull(entitySet);

            assertEquals(2, entitySet.getCount());

            for (ODataEntity entity : entitySet.getEntities()) {
                final Integer key = entity.getProperty("OrderId").getPrimitiveValue().<Integer>toCastValue();
                final Integer customerId = entity.getProperty("CustomerId").getPrimitiveValue().<Integer>toCastValue();
                assertTrue(navigationKeys.contains(key));
                assertEquals(Integer.valueOf(id), customerId);
                navigationKeys.remove(key);
                final ODataDeleteRequest deleteReq = client.getCUDRequestFactory().getDeleteRequest(
                        URIUtils.getURI(testDefaultServiceRootURL, entity.getEditLink().toASCIIString()));

                deleteReq.setFormat(format);
                assertEquals(204, deleteReq.execute().getStatusCode());
            }

            return actualEntity;
        }
    }
    // get a Customer entity to be created

    public ODataEntity getNewCustomer(
            final int id, final String name, final boolean withInlineInfo) {

        final ODataEntity entity =
                ODataObjectFactory.newEntity("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer");

        // add name attribute
        entity.addProperty(ODataObjectFactory.newPrimitiveProperty("Name",
                client.getPrimitiveValueBuilder().setText(name).setType(EdmSimpleType.String).build()));

        // add key attribute
        if (id != 0) {
            entity.addProperty(ODataObjectFactory.newPrimitiveProperty("CustomerId",
                    client.getPrimitiveValueBuilder().setText(String.valueOf(id)).setType(EdmSimpleType.Int32).build()));
        }
        final ODataCollectionValue backupContactInfoValue = new ODataCollectionValue(
                "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails)");


        final ODataComplexValue contactDetails = new ODataComplexValue(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails");


        final ODataCollectionValue altNamesValue = new ODataCollectionValue("Collection(Edm.String)");
        altNamesValue.add(client.getPrimitiveValueBuilder().
                setText("My Alternative name").setType(EdmSimpleType.String).build());
        contactDetails.add(ODataObjectFactory.newCollectionProperty("AlternativeNames", altNamesValue));

        final ODataCollectionValue emailBagValue = new ODataCollectionValue("Collection(Edm.String)");
        emailBagValue.add(client.getPrimitiveValueBuilder().
                setText("altname@mydomain.com").setType(EdmSimpleType.String).build());
        contactDetails.add(ODataObjectFactory.newCollectionProperty("EmailBag", emailBagValue));

        final ODataComplexValue contactAliasValue = new ODataComplexValue(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.Aliases");
        contactDetails.add(ODataObjectFactory.newComplexProperty("ContactAlias", contactAliasValue));

        final ODataCollectionValue aliasAltNamesValue = new ODataCollectionValue("Collection(Edm.String)");
        aliasAltNamesValue.add(client.getPrimitiveValueBuilder().
                setText("myAlternativeName").setType(EdmSimpleType.String).build());
        contactAliasValue.add(ODataObjectFactory.newCollectionProperty("AlternativeNames", aliasAltNamesValue));

        final ODataComplexValue homePhone = new ODataComplexValue(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone");
        homePhone.add(ODataObjectFactory.newPrimitiveProperty("PhoneNumber",
                client.getPrimitiveValueBuilder().setText("8437568356834568").setType(EdmSimpleType.String).build()));
        homePhone.add(ODataObjectFactory.newPrimitiveProperty("Extension",
                client.getPrimitiveValueBuilder().setText("124365426534621534423ttrf").setType(EdmSimpleType.String).
                build()));
        contactDetails.add(ODataObjectFactory.newComplexProperty("HomePhone", homePhone));

        backupContactInfoValue.add(contactDetails);
        entity.addProperty(ODataObjectFactory.newCollectionProperty("BackupContactInfo",
                backupContactInfoValue));
        if (withInlineInfo) {
            final ODataInlineEntity inlineInfo = ODataObjectFactory.newInlineEntity("Info", URI.create("Customer(" + id
                    + ")/Info"), getInfo(id, name + "_Info"));
            inlineInfo.getEntity().setMediaEntity(true);
            entity.addLink(inlineInfo);
        }

        return entity;
    }
    //delete an entity and associated links after creation

    public void delete(final ODataPubFormat format, final ODataEntity created, final boolean includeInline,
            final String baseUri) {
        final Set<URI> toBeDeleted = new HashSet<URI>();
        toBeDeleted.add(created.getEditLink());

        if (includeInline) {
            for (ODataLink link : created.getNavigationLinks()) {
                if (link instanceof ODataInlineEntity) {
                    final ODataEntity inline = ((ODataInlineEntity) link).getEntity();
                    if (inline.getEditLink() != null) {
                        toBeDeleted.add(URIUtils.getURI(baseUri, inline.getEditLink().toASCIIString()));
                    }
                }

                if (link instanceof ODataInlineEntitySet) {
                    final ODataEntitySet inline = ((ODataInlineEntitySet) link).getEntitySet();
                    for (ODataEntity entity : inline.getEntities()) {
                        if (entity.getEditLink() != null) {
                            toBeDeleted.add(URIUtils.getURI(baseUri, entity.getEditLink().toASCIIString()));
                        }
                    }
                }
            }
        }
        assertFalse(toBeDeleted.isEmpty());

        for (URI link : toBeDeleted) {
            final ODataDeleteRequest deleteReq = client.getCUDRequestFactory().getDeleteRequest(link);
            final ODataDeleteResponse deleteRes = deleteReq.execute();

            assertEquals(204, deleteRes.getStatusCode());
            assertEquals("No Content", deleteRes.getStatusMessage());

            deleteRes.close();
        }
    }
    // add Information property

    public ODataEntity getInfo(final int id, final String info) {
        final ODataEntity entity =
                ODataObjectFactory.newEntity("Microsoft.Test.OData.Services.AstoriaDefaultService.CustomerInfo");
        entity.setMediaEntity(true);

        entity.addProperty(ODataObjectFactory.newPrimitiveProperty("Information",
                client.getPrimitiveValueBuilder().setText(info).setType(EdmSimpleType.String).build()));
        return entity;
    }
    // validate newly created entities

    public ODataEntity validateEntities(final String serviceRootURL,
            final ODataPubFormat format,
            final ODataEntity original,
            final int actualObjectId,
            final Collection<String> expands, final String entitySetName) {

        final URIBuilder uriBuilder = client.getURIBuilder(serviceRootURL).
                appendEntityTypeSegment(entitySetName).appendKeySegment(actualObjectId);

        if (expands != null) {
            for (String expand : expands) {
                uriBuilder.expand(expand);
            }
        }
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(format);

        final ODataRetrieveResponse<ODataEntity> res = req.execute();
        assertEquals(200, res.getStatusCode());

        final ODataEntity actual = res.getBody();
        assertNotNull(actual);

        validateLinks(original.getAssociationLinks(), actual.getAssociationLinks());
        validateLinks(original.getEditMediaLinks(), actual.getEditMediaLinks());
        validateLinks(original.getNavigationLinks(), actual.getNavigationLinks());

        checkProperties(original.getProperties(), actual.getProperties());
        return actual;
    }
    // compares links of the newly created entity with the previous 

    public void validateLinks(final Collection<ODataLink> original, final Collection<ODataLink> actual) {
        assertTrue(original.size() <= actual.size());

        for (ODataLink originalLink : original) {
            ODataLink foundOriginal = null;
            ODataLink foundActual = null;

            for (ODataLink actualLink : actual) {

                if (actualLink.getType() == originalLink.getType()
                        && (originalLink.getLink() == null
                        || actualLink.getLink().toASCIIString().endsWith(originalLink.getLink().toASCIIString()))
                        && actualLink.getName().equals(originalLink.getName())) {

                    foundOriginal = originalLink;
                    foundActual = actualLink;
                }
            }

            assertNotNull(foundOriginal);
            assertNotNull(foundActual);

            if (foundOriginal instanceof ODataInlineEntity && foundActual instanceof ODataInlineEntity) {
                final ODataEntity originalInline = ((ODataInlineEntity) foundOriginal).getEntity();
                assertNotNull(originalInline);

                final ODataEntity actualInline = ((ODataInlineEntity) foundActual).getEntity();
                assertNotNull(actualInline);

                checkProperties(originalInline.getProperties(), actualInline.getProperties());
            }
        }
    }
    // compares properties of the newly created entity with the properties that were originally provided

    public void checkProperties(final Collection<ODataProperty> original, final Collection<ODataProperty> actual) {
        assertTrue(original.size() <= actual.size());

        final Map<String, ODataProperty> actualProperties = new HashMap<String, ODataProperty>(actual.size());

        for (ODataProperty prop : actual) {
            assertFalse(actualProperties.containsKey(prop.getName()));
            actualProperties.put(prop.getName(), prop);
        }

        assertTrue(actual.size() <= actualProperties.size());

        for (ODataProperty prop : original) {
            assertNotNull(prop);
            if (actualProperties.containsKey(prop.getName())) {
                final ODataProperty actualProp = actualProperties.get(prop.getName());
                assertNotNull(actualProp);

                if (prop.getValue() != null && actualProp.getValue() != null) {
                    checkPropertyValue(prop.getName(), prop.getValue(), actualProp.getValue());
                }
            }
        }
    }
    // compares property value of the newly created entity with the property value that were originally provided

    public void checkPropertyValue(final String propertyName,
            final ODataValue original, final ODataValue actual) {

        assertNotNull("Null original value for " + propertyName, original);
        assertNotNull("Null actual value for " + propertyName, actual);

        assertEquals("Type mismatch for '" + propertyName + "'",
                original.getClass().getSimpleName(), actual.getClass().getSimpleName());

        if (original.isComplex()) {
            final List<ODataProperty> originalPropertyValue = new ArrayList<ODataProperty>();
            for (ODataProperty prop : original.asComplex()) {
                originalPropertyValue.add(prop);
            }

            final List<ODataProperty> actualPropertyValue = new ArrayList<ODataProperty>();
            for (ODataProperty prop : (ODataComplexValue) actual) {
                actualPropertyValue.add(prop);
            }

            checkProperties(originalPropertyValue, actualPropertyValue);
        } else if (original.isCollection()) {
            assertTrue(original.asCollection().size() <= actual.asCollection().size());

            boolean found = original.asCollection().isEmpty();

            for (ODataValue originalValue : original.asCollection()) {
                for (ODataValue actualValue : actual.asCollection()) {
                    try {
                        checkPropertyValue(propertyName, originalValue, actualValue);
                        found = true;
                    } catch (AssertionError error) {
                    }
                }
            }

            assertTrue("Found " + actual + " and expected " + original, found);
        } else {
            assertTrue("Primitive value for '" + propertyName + "' type mismatch",
                    original.asPrimitive().getTypeName().equals(actual.asPrimitive().getTypeName()));

            assertEquals("Primitive value for '" + propertyName + "' mismatch",
                    original.asPrimitive().toString(), actual.asPrimitive().toString());
        }
    }
}
