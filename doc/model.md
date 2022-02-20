# Create .gitignore for external models
```
cat .gitignore > model-gitignore.txt
```

```
find * -iwholename "*kt" -o -iwholename "*kts" -o -iwholename "gradle*" >> model-gitignore.txt
```
