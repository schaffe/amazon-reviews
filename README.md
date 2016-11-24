# Amazon food analyzer
The goal of this task is to analyze and transform the > 500.000 reviews from Amazon.
Please go to Kaggle.com and download (please take the 300MB csv file):
https://www.kaggle.com/snap/amazon-fine-food-reviews

## Task description
* Finding 1000 most active users (profile names)
* Finding 1000 most commented food items (item ids).
* Finding 1000 most used words in the reviews
* We want to translate all the reviews using Google Translate API. You can send up to 1000 characters per HTTP call. API has 200ms average response time. How to do it efficiently and cost effective (you pay for API calls and we have concurrency limits - 100 requests in parallel max) - please mock the calls to google translate API.

Result to be printed of point: 1,2,3 to standard output sorted alphabetically and executes point 4 to mocked 
endpoint (when launched with the argument ‘translate=true’)

### Requirements
* Using full multi core CPU power.
* Memory limitation - 500MB of RAM. (Using Xmx500m VM option).

### Technologies used
* Akka
* Commons CSV

## How to build
* Use `gradlew` ot `gradlew.bat` script to build project. See more at https://docs.gradle.org/current/userguide/gradle_wrapper.html.
* Run application jar with main class `com.dzidzoiev.amazonreviews.Invoker`.

Program arguments

1. Path to file
1. Number of items to process
1. Parameter ‘translate=true’ - translate reviews (mocked)
    
## Memory consumption
Analyzed with jconsole
![alt tag](monitor/jconsole.png)
