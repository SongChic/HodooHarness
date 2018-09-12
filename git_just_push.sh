#text="$1"
if [ ! -n "$1" ]
then
	echo "comment를 입력해주세요."
	exit
elif [ ! -n "$2" ]
then
	echo "branch를 입력해주세요."
	exit
fi

echo "git start"

git add .
git commit -m "$1"
git push origin $2
