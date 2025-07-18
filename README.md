# localstack-access-point
(Non working) Example of a Spring Boot Application that attempts to create an s3 access point with localstack

This is for feedback purposes only.  Currently it fails to work with a NPE

```
host must not be null.
java.lang.NullPointerException: host must not be null.
	at software.amazon.awssdk.utils.Validate.paramNotNull(Validate.java:156)
	at software.amazon.awssdk.http.DefaultSdkHttpFullRequest.<init>(DefaultSdkHttpFullRequest.java:56)
	at software.amazon.awssdk.http.DefaultSdkHttpFullRequest.<init>(DefaultSdkHttpFullRequest.java:44)
	at software.amazon.awssdk.http.DefaultSdkHttpFullRequest$Builder.build(DefaultSdkHttpFullRequest.java:482)
	at software.amazon.awssdk.http.DefaultSdkHttpFullRequest$Builder.build(DefaultSdkHttpFullRequest.java:250)
	at software.amazon.awssdk.services.s3control.endpoints.internal.AwsEndpointProviderUtils.setUri(AwsEndpointProviderUtils.java:152)
	at software.amazon.awssdk.services.s3control.endpoints.internal.S3ControlRequestSetEndpointInterceptor.modifyHttpRequest(S3ControlRequestSetEndpointInterceptor.java:35)
```

Under the hood the application resolving the URL to http://000000000000.127.0.0.1:32793   (accountId + localhost) which causes it not to be able to resolve the hostname.
