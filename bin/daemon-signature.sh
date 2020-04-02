curl https://s3.dualstack.us-west-2.amazonaws.com/aws-xray-assets.us-west-2/xray-daemon/aws-xray-daemon-macos-3.x.zip -o daemon.zip
unzip daemon.zip
./xray_mac --version
curl https://s3.dualstack.us-west-2.amazonaws.com/aws-xray-assets.us-west-2/xray-daemon/aws-xray-daemon-macos-3.x.zip.sig -o daemon.zip.sig
curl https://s3.dualstack.us-west-2.amazonaws.com/aws-xray-assets.us-west-2/xray-daemon/aws-xray.gpg -o aws-xray.gpg
gpg --import aws-xray.gpg
gpg --verify daemon.zip.sig daemon.zip
rm cfg.yaml xray_mac daemon.zip daemon.zip.sig aws-xray.gpg