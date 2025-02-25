function onAppLaunch(appName) {
    // Check if the app is in the control list
    if (controlList.includes(appName)) {
        showPopupMessage(); // This should be called immediately
    }
    // ...
} 