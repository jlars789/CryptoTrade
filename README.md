# CryptoTrade
A program that trades crypto-currency on the CoinBase website - Created by Joseph Larson

Disclaimer: I do not endorse cryptocurrency as a legitimate means of investment. I also do not endorse CoinBase as a platform. Also, none of the below methods have made any promises to generate income with any guarantee. 

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
