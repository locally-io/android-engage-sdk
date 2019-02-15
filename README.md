# EngageSDK
[![Build Status](https://travis-ci.org/locally-io/android-engage-sdk.svg?branch=master)](https://travis-ci.org/locally-io/android-engage-sdk)

This document contains the first steps to integrate EngageSDK to your application, some of the steps may change due different ways to make the process.

## Requirementes

  - Android Studio
  - API 19: Android 4.4 (Kitkat) or above
  - Firebase Cloud Messaging key (for push notifications)
  - Locally Keys
  
## Installation
**1.**  Open `build.gradle (module: app)` and add the following to your `dependencies {}`.


```groovy
        //library
        implementation 'io.locally:engage-core:1.0.1'
        //Android 9 or above
        implementation 'commons-logging:commons-logging:1.1.1'
        //Locations
        implementation 'com.google.android.gms:play-services-location:15.0.1'
        implementation 'com.google.android.gms:play-services-ads:15.0.1'
        //Beacons
        implementation 'com.kontaktio:sdk:4.0.2'
        implementation 'com.squareup.retrofit2:adapter-rxjava2:2.4.0'
        //Images
        implementation 'com.squareup.picasso:picasso:2.71828'
        //Notifications
        implementation 'com.amazonaws:aws-android-sdk-cognitoidentityprovider:2.6.8'
        implementation 'com.amazonaws:aws-android-sdk-sns:2.2.11'
        implementation 'com.google.firebase:firebase-messaging:17.3.4'
```

**2.**  Enable the multidex at your `defaultConfig{}` like this

```groovy
    defaultConfig {
        ....
        multiDexEnabled true
        ...
    }
```

## Usage
- **Initialize**

    This step is necessary in order to continue using the _EngageSDK_ features, make sure you initialize before any further use.

```Java
        class Activity : AppCompatActivity() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_layout)
                EngageSDK.init(this)
                ...
            }
        }
```

- **Login**
    
    After initializing you may log into _**Locally platform**_ using your app keys. This method returns an `AuthStatus` depending on the response from the server (SUCCESS, UNAUTHORIZED, CONNECTION_ERROR, UNKNOWN_ERROR)

```Java
        private fun performLogin(){
            EngageSDK.login("YOUR APP USERNAME", "YOUR APP PASSWORD") { status, message -> 
                when(status) {
                    AuthStatus.SUCCESS -> { /* login success */ }
                    AuthStatus.UNAUTHORIZED -> { /* wrong username/password */ }
                    AuthStatus.CONNECTION_ERROR -> { /* failed trying to connect */ }
                    AuthStatus.UNKNOWN_ERROR -> { /* unknown error */ System.out.println(message) }
                }
            }
        }
```

- **Monitoring**
    
    EngageSDK provides a monitor to search for surrounding content based on your position or nearby bluetooth devices(**beacons**).

```Java
        private fun startMonitoring(){
            EngageSDK.startMonitoringBeacons() //to monitor for nearby beacons
            EngageSDK.startMonitoringGeofences() //to monitor your current location
        }
```

*       You can also stop monitoring like the following.
        
```Java
        private fun stopMonitoring(){
            EngageSDK.stopMonitoringBeacons() 
            EngageSDK.stopMonitoringGeofences() 
        }
```

- **Handling Content**

    In order to handle content received you need to implement the interface **CampaignListener** which contains the method _didCampaignArrived(intent: Intent)_.

```Java
        class MainActivity : AppCompatActivity() {             
            fun handleCampaignContent() {
                EngageSDK.setListener(object: EngageSDK.CampaignListener {
                    override fun didCampaignArrived(campaign: CampaignContent?) {
                        //Handle campaign
                        getContent(campaign)
                    }
                })
            }
        }
```
>You can just launch the value `campaign` using the default widget handler `WidgetsPresenter`:

>`WidgetPresenters` takes the content provided and tries to open it as an Activity if the application is on top, otherwise it sends a notification. 
    
```Java
        private fun getContent(campaign: CampaignContent?){
            WidgetsPresenter.presentWidget(applicationContext, campaign)
        }
```
    
- **Enabling push notifications**

    To enable push notifications to your device, you will need to provide your Firebase token like the following. This function also provides a response if successful `true` or `false` otherwise.

>       You may need to integrate *FirebaseCloudMessaging* to retrieve your FCM token 

```Java
        private fun enablePushNotifications(){
            EngageSDK.enablePushNotifications("YOUT FIREBASE TOKEN") { //here you can handle the response
                if(it) System.out.println("Push notifications enabled")
                else System.out.println("Push notification error")
            }
        }
```

for more info, please visit [developer web site.](https://locally.io/developers/)

## Author
urosas@sahuarolabs.com

## License
EngageSDK is available under the MIT license. See the LICENSE file for more info.
