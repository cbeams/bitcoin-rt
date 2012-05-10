
## install and run mongodb
see http://www.mongodb.org/display/DOCS/Quickstart+OS+X
```
brew install mongodb # (~50MB)
sudo mkdir -p /data/db/
sudo chown `id -u` /data/db
mongod
```

## install npm/node
```
brew install npm
brew install node
```

## run the app
```
git clone git://github.com/cbeams/bitcoin-rt.git
cd bitcoin-rt
npm install websockets
npm install mongodb
node bitcoind.js
open http://localhost:1337
```
