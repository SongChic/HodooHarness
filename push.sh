#!/bin/bash
#text="$1"
commit=""
branch=master
if [ ! -n "$1" ]
then
	read -n 1 -p "comment 없이 push 하시겠습니까? (Y/n) : "
	echo -e "\n$REPLY"
	if [ "$REPLY" != "Y" -a "$REPLY" != "y" ]
	then
		read -p "commit 내용을 입력해주세요 : " commit
		echo -e "\n$commit"
	fi
	#exit
fi
if [ ! -n "$2" ]
then
	read -n 1 -p "master branch를 사용하시겠습니까? (Y/n)"
	if [ "$REPLY" == "Y" -o "$REPLY" == "y" ]
	then
		branch=master
	else
		echo -e "\n"
		read -p "사용할 브랜치를 입력해주세요 : " temp_branch
		echo -e "\n$temp_branch"
		branch=$temp_branch
	fi
	#exit
fi

echo "git start"

echo "$commit"
echo "$branch"

git add .
git commit -m "$commit"
git push origin $branch
