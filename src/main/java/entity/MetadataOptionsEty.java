package entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE)
public class MetadataOptionsEty {

    private String HttpTokens;
    private String HttpEndpoint;

    public MetadataOptionsEty() {
    }

    public String getHttpTokens() {
        return HttpTokens;
    }

    public void setHttpTokens(String httpTokens) {
        HttpTokens = httpTokens;
    }

    public String getHttpEndpoint() {
        return HttpEndpoint;
    }

    public void setHttpEndpoint(String httpEndpoint) {
        HttpEndpoint = httpEndpoint;
    }
}
