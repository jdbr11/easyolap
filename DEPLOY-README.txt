׼����


ʵʩ��

���ɼ��ܺ�������ļ���
1.�޸�common-encryption/src/main/resources/config.properties ������ܵ�key
2.�޸�common-encryption/src/main/resources/src.properties �����Ҫ���ܵ����ݡ�
3.����common-encryption�е�CodeGenerator�࣬���ɼ��ܺ�������ļ�safe-config.properties


��ʹ�û����м��ؽ�����ͼ��ܺ�������ļ�
1.���ܻ�������
common-utils
common-decryption
2.����ʾ��
String decryptValue = DecryptAESCoder.decrypt(propertyValue, encryptKey);
propertyValue�������ܵ�����
encryptKey�����ܵ�key
decryptValue:���ܺ������



���Ϊ��ִ�е�jar
mvn clean install assembly:assembly -Dmaven.test.skip=true -rf :module-olap


����ͨ��"mvn help:effective-pom"��ȡ��������Ч��pom.xml

�ύ�汾˵����
���ύ�汾ǰ�������ļ����м�����ļ����Լ���־��������
mvn clean eclipse:clean


����eclipse˵����
��mavenģʽֱ�ӵ��뵽eclipse�м��ɡ�