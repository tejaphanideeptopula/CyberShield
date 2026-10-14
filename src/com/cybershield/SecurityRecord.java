package com.cybershield;

public class SecurityRecord {

    private String time;
    private String protocol;
    private String flag;
    private String family;
    private String clusters;
    private String seedAddress;
    private String expAddress;
    private String btc;
    private String usd;
    private String netflowBytes;
    private String ipAddress;
    private String threats;
    private String port;
    private String prediction;

    public SecurityRecord(
            String time,
            String protocol,
            String flag,
            String family,
            String clusters,
            String seedAddress,
            String expAddress,
            String btc,
            String usd,
            String netflowBytes,
            String ipAddress,
            String threats,
            String port,
            String prediction) {

        this.time = time;
        this.protocol = protocol;
        this.flag = flag;
        this.family = family;
        this.clusters = clusters;
        this.seedAddress = seedAddress;
        this.expAddress = expAddress;
        this.btc = btc;
        this.usd = usd;
        this.netflowBytes = netflowBytes;
        this.ipAddress = ipAddress;
        this.threats = threats;
        this.port = port;
        this.prediction = prediction;
    }

    public String getTime() {
        return time;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getFlag() {
        return flag;
    }

    public String getFamily() {
        return family;
    }

    public String getClusters() {
        return clusters;
    }

    public String getSeedAddress() {
        return seedAddress;
    }

    public String getExpAddress() {
        return expAddress;
    }

    public String getBtc() {
        return btc;
    }

    public String getUsd() {
        return usd;
    }

    public String getNetflowBytes() {
        return netflowBytes;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getThreats() {
        return threats;
    }

    public String getPort() {
        return port;
    }

    public String getPrediction() {
        return prediction;
    }

    public String toSearchText() {
        // Searchable text uses descriptive identifiers/categories. Time,
        // numeric payment/flow fields and Prediction are intentionally left
        // out because their semantics/scales are not established here.
        return "Protocol=" + protocol +
                " Flag=" + flag +
                " Family=" + family +
                " SeedAddress=" + seedAddress +
                " ExpAddress=" + expAddress +
                " IPaddress=" + ipAddress +
                " Threats=" + threats +
                " Port=" + port;
    }

    @Override
    public String toString() {
        return toSearchText();
    }
}
