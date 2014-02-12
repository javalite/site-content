curl -O http://python.org/ftp/python/2.7.6/Python-2.7.6.tgz
tar xvf Python-2.7.6.tgz
cd Python-2.7.6
./configure --prefix /opt/python2.7.6
make
sudo make install
cd ..
rm -rf Python-2.7.6
rm Python-2.7.6.tgz

sudo pip install virtualenv