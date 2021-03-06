Most bluetooth headsets can't automatically switch among different computers or phones. You have to first disconnect the headset and then connect another device to it. BluePreempt is a tool helping to connect your bluetooth headset occupied by another device in a preemptive way. 

### Usage

BluePreempt has a **Windows** client named "BluePreempt" and a **Android** client named "ABluePreempt". Thus it supports switching a bluetooth headset among Windows or phones.

![](https://github.com/Megre/BluePreempt/blob/main/readme/Windows-client.png)

![](https://github.com/Megre/BluePreempt/blob/main/readme/Android-client.jpg)

Windows / Android clients are quite similar. The functions are performed with three buttons: Scan, Mark, and Connect.

1. Open BluePreempt / ABluePreempt on computers and phones to enable the switch of headset among these devices.
1. **Scan** bluetooth devices.
2. Select and **Mark** the bluetooth headset that allows other devices to disconnect.
3. **Connect** to the headset on the computer / phone in use.



### Dependence

##### Android Client

The Android client needs the following permissions:

- Bluetooth Permission
- Location Permission
- GPS service

Please add ABluePreempt to **Battery Optimization Whitelist** in order to respond to remote requests in background.

##### Windows Client

The Windows client depends on:

- [Bluetooth Command Line Tools](http://bluetoothinstaller.com/bluetooth-command-line-tools/). Please install this tool and select the `add to the system PATH` option during the installation.
- [JRE 8 (Windows x86)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html). Please install JRE version 8 (minimal) and [configure the system PATH](https://docs.oracle.com/javase/tutorial/essential/environment/paths.html).