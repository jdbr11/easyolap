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