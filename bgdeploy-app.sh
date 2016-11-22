#!/bin/bash
# Use this script to deploy your application onto a newer buildpack with zero downtime

if [ "$#" -ne 3 ]
then
  echo "Usage: bgdeploy.sh originalAppName backupAppName fullyQualifiedPathToNewApp"
  echo ""
  echo "This script will rename originalAppName to backupAppName. "\
       "Then it will push the artifact specified on the third argument"\
       "using the attributes of originalAppName via a manifest.yml file."
  echo " "
  echo "NOTE 1: You must be logged in to Cloud Foundry to run this command."
  echo "NOTE 2: The minimum version supported of cf command line client is 6.1.1."
  echo "NOTE 3: To capture output use:"
  echo "             bgdeploy.sh arg1 arg2 arg3 2>&1 | tee results.txt"
  exit 1
fi

if ! cf --version | awk '{print $3}' | grep -q -e "^6\.";
  then echo "cf version must be 6"; exit 1;
fi

echo "originalAppName..[$1]"
echo "backupAppName....[$2]"
echo "pathToApp........[$3]"


#CF_COLOR=false gets rid of hidden chars in output
export CF_COLOR=false

if ! cf app "$1" >&/dev/null;
  then echo "originalAppName $1 does not exist"; exit 1;
fi

if cf app "$2">&/dev/null;
  then echo "backupAppName $2 already exists"; exit 1;
fi

if [ ! -e $3 ];
  then echo "Path $3 does not exist"; exit 1;
fi

manifestName="$1_manifest.yml"
if [ -e $manifestName ];
  then echo "Manifest.yml file [$manifestName] already exists. Please delete or rename it"; exit 1;
fi

#capture the output of 'cf app' command for app $1
data1=`cf app "$1"`

#extract the number of instances of app $1
if numInstances=`expr "$data1" : '.*instances:[^0-9]*[0-9]\+\/\([0-9]\+\)'`;
then
  echo "numInstances=$numInstances"
else
  echo "numInstances not found"
  exit
fi

#extract the memsize of app $1
if memSize=`expr "$data1" : '.*usage:[^0-9]*\([0-9]*[MG]\)'`
then
  echo "memSize=$memSize"
else
  echo "memSize not found"
  exit
fi

#extract the services of app $1
svcLines="  services:\n"
readarray svcarray < <(cf services | grep "$1")
for item in "${svcarray[@]}"
do
  service=`echo ${item} | awk '{printf $1}'`
  svcLines="$svcLines  - $service\n"
done


#extract the urls of app $1
urlLine=`echo "$data1" | grep -e "^urls: "`
if urls=`expr "$urlLine" : 'urls: \(.*\)'`;
then
  echo "urls=[$urls]"
else
  echo "urls not found"
  exit
fi

arrayOfUrls=( $urls )
firstUrl=${arrayOfUrls[0]}
if domain=`expr "$firstUrl" : '[^\.]*\.\([^,]*\)'`;
then
  echo "domain=[$domain]"
else
  echo "domain not found"
  exit
fi

hostArray=""
for url in $urls;
do
  if host=`expr "$url" : '\([^\.]*\).*'`;
  then
    hostArray="$hostArray $host"
  else
    echo "host not found"
    exit
  fi
done

echo -e "#$manifestName"           >$manifestName
echo -e "domain: $domain"         >>$manifestName
echo -e "applications:"           >>$manifestName
echo -e "- name: $1"              >>$manifestName
echo -e "  memory: $memSize"      >>$manifestName
echo -e "  path: $3"              >>$manifestName
echo -e "  no-route: true"        >>$manifestName
echo -e "$svcLines"               >>$manifestName


echo " "
echo "Preparing to rename $1 to $2 then push $1 with the following mmanifest.yml file [$manifestName]"
echo "Routes will be mapped and the app will be started after the push."
echo " "
while read line
do
  echo -e "$line"
done <$manifestName
echo " "

echo "Do you want to rename $1 to $2 and push $1 using $manifestName? [y/n]"
read
if [ $REPLY = 'y' ];
then
  cf rename $1 $2
  cf push -f $manifestName --no-start
else
  echo "Not renaming or pushing $1, exiting."
  exit
fi

for host in $hostArray
do
  cf map-route $1 $domain -n $host
done

cf start $1

echo " "
cf apps

echo "Do you want to delete $2? [y/n]"
read
if [ $REPLY = 'y' ];
then
  cf delete $2 -f
  cf apps
else
  echo "Not deleting $2"
fi

echo "Do you want to delete $manifestName? [y/n]"
read
if [ $REPLY = 'y' ];
then
  rm $manifestName -f
else
  echo "Not deleting $manifestName"
fi
