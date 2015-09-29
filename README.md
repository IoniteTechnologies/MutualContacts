# MutualContacts

-------------------------------------------------------------------------
Copyright 2015 Shivakshi Chaudhary/Ionite Technologies LLP

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

-------------------------------------------------------------------------

Displays database join of Android phone contacts and registered users of app after user logs in with his phone number which is authenticated by SMS verification code.
The list displayed is a mutual list of contacts who are using the Android application.

This is a Log-in module like Whatsapp. Now Open Source. Install Fiflow Android App to see how it works. Please donate by upgrading to full-version. Fiflow: https://play.google.com/store/apps/details?id=com.fiflow
Thanks!

Steps:
1. Go to AWS Console: https://console.aws.amazon.com/ and create account.

2. Go to RDS services under database https://console.aws.amazon.com/rds/home?region=us-east-1

3. Create instance, click "Launch DB instance" -> Select "MySQL" -> Select "No, this instance is intended for use outside of production or under the RDS Free Usage Tier" -> Create.

4. Update credentials created in step 3 in com.ionitetechnologies.mutualcontacts.server.servlet.DatabaseUtil.java as follows:
  	String url ="jdbc:mysql://xxxxinstance.xxxxxxx.us-east-1.rds.amazonaws.com:3306/";
    String userName= "xxxxxxxxx";
    String password= "xxxxxxxxx";
    String dbName = "xxxxxxxx";

5. Update your AWS Credentials from http://aws.amazon.com/security-credentials in MutualContactsServer\build\classes\AwsCredentials.properties
  secretKey=**************************************** (40 characters)
  accessKey=******************** (20 characters)

6. Update com.ionitetechnologies.mutualcontacts.server.constants.Constants.java:
    public static final String AWS_SECRET_KEY = "****************************************";

7. Go to EC2 services in AWS console, create instance and update server URL in com.ionitetechnologies.mutualcontacts.servertools.CommonUtilities.java
    public static final String SERVER_URL = "http://ec2-xx-xx-x-xxx.compute-1.amazonaws.com:8080/xxxserver";

8. Since app uses GCM (Google Cloud Messaging) Services, log-in on Goggle Apps Console and create GCM Sender ID.
Update in following files:
com.ionitetechnologies.mutualcontacts.servertools.CommonUtilities.java
	    public static final String SENDER_ID = "000000000000";
res/strings.xml
    <string name="gcm_project_id">000000000000</string>
      
That's it. You're good to go!


Required Third-Party Jar Libraries:
Client:
gcm.jar, 
gson-2.2.2.jar, 
gson-2.2.2-sources.jar, 
aws-android-sdk.jar
Server:
gcm-server.jar, 
gson-2.2.2.jar, 
json_simple-1.1.jar, 
mysql-connector-java-5.1.22-bin.jar, 
persistence-api-1.0.jar, 
servlet-api-2.5.jar, 
SimpleJPA-1.5.1.jar

For any queries/bugs, please contact:
shivakshichaudhary@gmail.com
