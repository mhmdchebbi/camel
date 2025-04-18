/* Generated by camel build tools - do NOT edit this file! */
package org.apache.camel.component.azure.storage.blob;

import javax.annotation.processing.Generated;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.camel.spi.EndpointUriFactory;

/**
 * Generated by camel build tools - do NOT edit this file!
 */
@Generated("org.apache.camel.maven.packaging.GenerateEndpointUriFactoryMojo")
public class BlobEndpointUriFactory extends org.apache.camel.support.component.EndpointUriFactorySupport implements EndpointUriFactory {

    private static final String BASE = ":accountName/containerName";

    private static final Set<String> PROPERTY_NAMES;
    private static final Set<String> SECRET_PROPERTY_NAMES;
    private static final Set<String> MULTI_VALUE_PREFIXES;
    static {
        Set<String> props = new HashSet<>(54);
        props.add("accessKey");
        props.add("accountName");
        props.add("backoffErrorThreshold");
        props.add("backoffIdleThreshold");
        props.add("backoffMultiplier");
        props.add("blobName");
        props.add("blobOffset");
        props.add("blobSequenceNumber");
        props.add("blobServiceClient");
        props.add("blobType");
        props.add("blockListType");
        props.add("bridgeErrorHandler");
        props.add("changeFeedContext");
        props.add("changeFeedEndTime");
        props.add("changeFeedStartTime");
        props.add("closeStreamAfterRead");
        props.add("closeStreamAfterWrite");
        props.add("commitBlockListLater");
        props.add("containerName");
        props.add("createAppendBlob");
        props.add("createPageBlob");
        props.add("credentialType");
        props.add("credentials");
        props.add("dataCount");
        props.add("delay");
        props.add("downloadLinkExpiration");
        props.add("exceptionHandler");
        props.add("exchangePattern");
        props.add("fileDir");
        props.add("greedy");
        props.add("initialDelay");
        props.add("lazyStartProducer");
        props.add("leaseBlob");
        props.add("leaseDurationInSeconds");
        props.add("maxResultsPerPage");
        props.add("maxRetryRequests");
        props.add("operation");
        props.add("pageBlobSize");
        props.add("pollStrategy");
        props.add("prefix");
        props.add("regex");
        props.add("repeatCount");
        props.add("runLoggingLevel");
        props.add("sasToken");
        props.add("scheduledExecutorService");
        props.add("scheduler");
        props.add("schedulerProperties");
        props.add("sendEmptyMessageWhenIdle");
        props.add("serviceClient");
        props.add("sourceBlobAccessKey");
        props.add("startScheduler");
        props.add("timeUnit");
        props.add("timeout");
        props.add("useFixedDelay");
        PROPERTY_NAMES = Collections.unmodifiableSet(props);
        Set<String> secretProps = new HashSet<>(2);
        secretProps.add("accessKey");
        secretProps.add("sourceBlobAccessKey");
        SECRET_PROPERTY_NAMES = Collections.unmodifiableSet(secretProps);
        Set<String> prefixes = new HashSet<>(1);
        prefixes.add("scheduler.");
        MULTI_VALUE_PREFIXES = Collections.unmodifiableSet(prefixes);
    }

    @Override
    public boolean isEnabled(String scheme) {
        return "azure-storage-blob".equals(scheme);
    }

    @Override
    public String buildUri(String scheme, Map<String, Object> properties, boolean encode) throws URISyntaxException {
        String syntax = scheme + BASE;
        String uri = syntax;

        Map<String, Object> copy = new HashMap<>(properties);

        uri = buildPathParameter(syntax, uri, "accountName", null, false, copy);
        uri = buildPathParameter(syntax, uri, "containerName", null, false, copy);
        uri = buildQueryParameters(uri, copy, encode);
        return uri;
    }

    @Override
    public Set<String> propertyNames() {
        return PROPERTY_NAMES;
    }

    @Override
    public Set<String> secretPropertyNames() {
        return SECRET_PROPERTY_NAMES;
    }

    @Override
    public Set<String> multiValuePrefixes() {
        return MULTI_VALUE_PREFIXES;
    }

    @Override
    public boolean isLenientProperties() {
        return false;
    }
}

