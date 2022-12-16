# CryptoTrade
A program that trades crypto-currency on the CoinBase website - Created by Joseph Larson

***UPDATE*** (Dec. 2022) Currently, this does not fully work. In this repository is a deprecated API key (try it if you want, it doesn't work) for sending text messages. The poor practice of including the API key in plaintext is a mistake made by 2020 me, and due to the fact I intend to publish this as an archive, I have no plans on fixing it. 

Disclaimer: I do not endorse cryptocurrency as a legitimate means of investment. I also do not endorse CoinBase as a platform. Also, none of the below methods have made any promises to generate income with any guarantee. This is an experiment into automated trading that, for me, did not prove excessively successful. 

## Introduction

This repository contains all of the Java code needed to create a .jar that runs the backend of a cryptocurrency trading agent. It works through basic data analysis to attempt to predict trajectory in the case you want it to invest for you (bad idea) and allows users to configure currrencies under four classes (described in investment types) that allow for either scalp-trading or long term investing. 

Within are capabilities for user notifications. This includes emails that detail actions taken that day and texts that notify the user of more "pressing" matters (like a large, long-term sale).

This is intended to be used with the [GUI Client]()

### For Servers
To setup EC2 Instance:
<ol>
<li> curl https://www.python.org/ftp/python/3.8.3/python-3.8.3-amd64.exe </li>
<li> Add python to PATH </li>
<li> Set following Environment Variables: </li>
<li> setx AWS_ACCESS_KEY_ID </li>
<li> setx AWS_SECRET_ACCESS_KEY </li>
<li> setx AWS_REGION </li>
<li> setx CB_ACCESS_KEY </li>
<li> setx CB_SECRET_KEY </li>
<li> setx CBPRO_ACCESS_KEY </li>
<li> setx CBPRO_SECRET_KEY </li>
<li> setx CBPRO_PASSPHRASE </li>
<li> Install AWS EB CLI: </li>
<li> pip install awsebcli --upgrade --user </li>
<li> Edit Path again to: </li>
<li> C:\User\Administrator\AppData\roaming\Python\Python37\scripts </li>
</ol>

Investment Types: <br/>
***ignored:*** This currency is not tracked at all <br/>
***watched:*** This currency is watched and analyzed; Currency will be included in updates <br/>
***short_term:*** This currency will be scalp-traded above a certain threshold, then repurchased when deemed a good investment by a machine learning algorithm <br/>
***long_term:*** This currency will be watched closely; Can be set to sell at a certain percentage profit margin.
