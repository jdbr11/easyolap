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


 checkstyle插件的可执行任务如下：
[plain] view plain copy print?
mvn checkstyle:help           查看checkstyle-plugin的帮助：   
mvn checkstyle:check          检查工程是否满足checkstyle的检查，如果没有满足，检查会失败，可以通过target/site/checkstyle.html查看。  
mvn checkstyle:checkstyle     检查工程是否满足checkstyle的检查，如果没有满足，检查不会失败，可以通过target/site/checkstyle.html查看。  
mvn checkstyle:checkstyle-aggregate     检查工程是否满足checkstyle的检查，如果没有满足，检查不会失败，可以通过target/site/checkstyle.html查看。  

