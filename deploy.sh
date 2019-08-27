#!/bin/bash

echo -e "\033[0;32mDeploying updates to GitHub...\033[0m"

git add .
git commit -m "public new content"
git push origin master

# Build the project.
temp_file=temp`date +"%s"`
zola build -o ${temp_file}
rm -r public/*

mv ${temp_file}/* ./public/
rm -r ${temp_file}

# Go To Public folder
cd public
# Add changes to git.
git add .

# Commit changes.
msg="rebuilding site `date`"
if [ $# -eq 1 ]
  then msg="$1"
fi

git commit -m "$msg"
git push origin master

# Come Back up to the Project Root
cd ..