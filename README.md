javac -d out (Get-ChildItem -Recurse -Filter *.java src).FullName
java -cp out main.Main  