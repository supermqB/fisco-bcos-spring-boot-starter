# fisco-bcos-spring-boot-starter

这是一个 fisco bcos 的java sdk starter，方便项目中使用sdk。
目前还没有deploy到maven central仓库。 所以可以采取拉取源码本地install的方式，或者deploy自己的私有仓库，来使用。

## 本地安装使用
- 拉取本项目，在项目目录下执行 `./mvnw clean install`

- 在要使用的项目pom.xml中添加依赖如下
```xml
<dependency>
    <groupId>com.cnhealth.devcenter</groupId>
    <artifactId>fisco-client-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

- 添加properties配置，以application.properties中的配置为例

  *注意，把密钥目录复制到bcos.cryptoMaterial.certPath定义的目录下*
```properties
# indicate BCOS SDK instance is needed
bcos.hasInstance=true
# where are key pairs
bcos.cryptoMaterial.certPath=conf

# The peer list to connect
bcos.network.peers[0]=127.0.0.1:20200
bcos.network.peers[1]=127.0.0.1:20201

# if user account is created, please config it here
bcos.account.keyStoreDir=account
bcos.account.accountFilePath=conf/0x3b525049e83aac4ee30cf662a47b64223d6edc4d.pem
bcos.account.accountFileFormat=pem
bcos.account.accountAddress=0x3b525049e83aac4ee30cf662a47b64223d6edc4d

bcos.threadPool.channelProcessorThreadSize=16
bcos.threadPool.receiptProcessorThreadSize=16
bcos.threadPool.maxBlockingQueueSize=102400
```
- 使用 `./sol2java.sh` 把写好的solidity合约转译成java类型，请参考官档。下面范例中的`BCOSLogger`就是转译后的class。

- 定义自己的合约service，为方便使用可以继承自`AbstractContractService`,下面是一个范例：
```java
@Component
public class BCOSLoggerService extends AbstractContractService {

    static Logger logger = LoggerFactory.getLogger(BCOSLoggerService.class);

    /* 使用链上已经部署合约，需配置。 */
    @Value("${bcos.contract.logger.address}")
    private String contractAddr;

    public String getContractAddress() {
        return this.contractAddr;
    };

    public void deployContract() throws ContractException {
        contractAddr = BCOSLogger.deploy(client, cryptoKeyPair).getContractAddress();
    }

    /* block chain functions are as below. */
    public LogAsset queryLog(String logId) {
        try {
            BCOSLogger loggerContract = BCOSLogger.load(contractAddr, client, cryptoKeyPair);
            Tuple3<List<String>, List<String>, List<String>> queryResult = loggerContract.query(logId);
            return new LogAsset(queryResult.getValue1().get(0), queryResult.getValue2().get(0),
                    queryResult.getValue3().get(0));
        } catch (Exception e) {
            /* ignore */
        }
        return new LogAsset();
    }
}
```

- 接下来，就可以在需要使用的地方，直接该service的方法，来使用合约的方法。