׼����


ʵʩ��




���Ϊ��ִ�е�jar

mvn clean package  -Dmaven.test.skip=true

mvn clean install assembly:assembly -Dmaven.test.skip=true -rf :module-olap


����ͨ��"mvn help:effective-pom"��ȡ��������Ч��pom.xml

�ύ�汾˵����
���ύ�汾ǰ�������ļ����м�����ļ����Լ���־��������
mvn clean eclipse:clean


����eclipse˵����
��mavenģʽֱ�ӵ��뵽eclipse�м��ɡ�


 checkstyle����Ŀ�ִ���������£�
[plain] view plain copy print?
mvn checkstyle:help           �鿴checkstyle-plugin�İ�����   
mvn checkstyle:check          ��鹤���Ƿ�����checkstyle�ļ�飬���û�����㣬����ʧ�ܣ�����ͨ��target/site/checkstyle.html�鿴��  
mvn checkstyle:checkstyle     ��鹤���Ƿ�����checkstyle�ļ�飬���û�����㣬��鲻��ʧ�ܣ�����ͨ��target/site/checkstyle.html�鿴��  
mvn checkstyle:checkstyle-aggregate     ��鹤���Ƿ�����checkstyle�ļ�飬���û�����㣬��鲻��ʧ�ܣ�����ͨ��target/site/checkstyle.html�鿴��  

