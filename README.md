## Connect to the EC2
Set correct permissions for the .pem:
```
chmod 400 /path/to/my-key.pem
```
Connect using SSH: Open a terminal and use the following command structure:
```
ssh -i /path/to/my-key.pem ubuntu@<your_ec2_public_ip>
```
The ec2 public ipv4 is 54.188.11.65.

## Start the minecraft server inside the EC2
Start a new screen session named "minecraft":
```
screen -S minecraft
```
Your terminal will clear. You are now inside the screen session. Start the server again:
```
cd main-server
java -Xms2G -Xmx2G -jar server.jar nogui
```
You can safely detach from this screen session by pressing `Ctrl+A`, then pressing the D key.

To re-attach to the server console later, use: `screen -r minecraft`.

## To upload files into the EC2
Open a new terminal in your computer (don't close the ssh connection).

Use the following command:
```
scp -i /path/to/my-key.pem /path/to/file ubuntu@<your_ec2_public_ip>:~/
```