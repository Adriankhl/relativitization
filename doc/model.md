# (Deprecated) Create .gitignore for external models
```shell
cat .gitignore > model-gitignore.txt
```

```shell
find * -iwholename "*kt" -o -iwholename "*kts" -o -iwholename "gradle*" >> model-gitignore.txt
```
