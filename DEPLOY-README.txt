准备：


实施：

生成加密后的配置文件。
1.修改common-encryption/src/main/resources/config.properties 定义加密的key
2.修改common-encryption/src/main/resources/src.properties 中添加要加密的内容。
3.运行common-encryption中的CodeGenerator类，生成加密后的配置文件safe-config.properties


在使用环境中加载解密类和加密后的配置文件
1.解密环境依赖
common-utils
common-decryption
2.解类示例
String decryptValue = DecryptAESCoder.decrypt(propertyValue, encryptKey);
propertyValue：待解密的内容
encryptKey：解密的key
decryptValue:解密后的明文



打包为可执行的jar
mvn clean install assembly:assembly -Dmaven.test.skip=true -rf :module-olap


可以通过"mvn help:effective-pom"获取到最终有效的pom.xml

提交版本说明：
在提交版本前把生成文件或中间过程文件，以及日志进行清理
mvn clean eclipse:clean


导入eclipse说明：
以maven模式直接导入到eclipse中即可。