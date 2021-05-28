package com.cnhealth.devcenter.fiscoclient;

import javax.annotation.PostConstruct;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractContractService {
    static Logger logger = LoggerFactory.getLogger(AbstractContractService.class);
    @Autowired
    private BcosSDK bcosSDK;

    protected Client client;
    protected CryptoKeyPair cryptoKeyPair;

    protected int clientGroupId() {
        return 1;
    };

    @PostConstruct
    public void init() throws Exception {
        client = bcosSDK.getClient(clientGroupId());
        cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        if (cryptoKeyPair == null) {
            cryptoKeyPair = client.getCryptoSuite().createKeyPair();
            client.getCryptoSuite().setCryptoKeyPair(cryptoKeyPair);
        }

        logger.info("create client for group{}, with account address: {}", clientGroupId(), cryptoKeyPair.getAddress());

        String contractAddr = getContractAddress();
        if (contractAddr == null || "".equals(contractAddr)) {
            try {
                deployContract();
                logger.info("deploy loggerContract success, contract address is {}", getContractAddress());
            } catch (Exception e) {
                logger.error("deploy Asset contract failed, error message is  " + e.getMessage());
            }
        }
    }

    public abstract void deployContract() throws ContractException;

    public abstract String getContractAddress();

}
