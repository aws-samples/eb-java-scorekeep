wget https://s3.dualstack.us-west-2.amazonaws.com/aws-xray-assets.us-west-2/xray-daemon/aws-xray-daemon-macos-2.x.zip
unzip aws-xray-daemon-macos-2.x.zip
./xray_mac --version
wget https://s3.dualstack.us-west-2.amazonaws.com/aws-xray-assets.us-west-2/xray-daemon/aws-xray-daemon-macos-2.x.zip.sig
wget https://s3.dualstack.us-west-2.amazonaws.com/aws-xray-assets.us-west-2/xray-daemon/aws-xray.gpg
gpg --import aws-xray.gpg
gpg --verify aws-xray-daemon-macos-2.x.zip.sig aws-xray-daemon-macos-2.x.zip
rm cfg.yaml xray_mac aws-xray-daemon-macos-2.x.zip aws-xray-daemon-macos-2.x.zip.sig aws-xray.gpg