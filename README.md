# stemmer
A utility to convert all plaintext documents in natural language stored in a user-defined directory to documents
containing word stems
<hr>

### Usage
`java -cp * com.github.janissl.DirectoryStemmer ${source_directory} ${destination_directory}`

The plaintext files must be UTF-8-encoded and named using the following pattern: `${title}_${language}.snt` where
`${language}` matches the language of the file content and represents an ISO 639-1 language code.
