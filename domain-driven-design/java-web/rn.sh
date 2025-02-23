find src -name "*.js" -type f | while read -r js_file; do
    py_file="${js_file%.js}.java"
    mv "$js_file" "$py_file"
    echo "Renamed $js_file to $py_file"
done
