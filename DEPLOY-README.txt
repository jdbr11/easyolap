准备：


实施：




打包为可执行的jar

mvn clean package  -Dmaven.test.skip=true

mvn clean install assembly:assembly -Dmaven.test.skip=true -rf :module-olap


可以通过"mvn help:effective-pom"获取到最终有效的pom.xml

提交版本说明：
在提交版本前把生成文件或中间过程文件，以及日志进行清理
mvn clean eclipse:clean


导入eclipse说明：
以maven模式直接导入到eclipse中即可。