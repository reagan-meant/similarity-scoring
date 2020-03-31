# similarity-scoring
An Elasticsearch plugin for scoring documents based on string similarity 

## Details
The Elasticsearch plugin  relies on the https://github.com/tdebatty/java-string-similarity library. 
The library is fullyopen source and publicly hosted on Github under the MIT licence. 

The plugin currently supports these algorithms:

1. Cosine similarity (cosine)
1. Jaccard index (jaccard)
1. Jaro-Winkler (jaro-winkler)
1. Longest Common Subsequence (longest-common-subsequence)
1. Normalized Levenshtein (levenshtein)
1. Sorensen-Dice coefficient (dice)

## Building
The plugin can be built with Java 13 with the following command:

```bash
./gradlew build
```

## Installation
The plugin installation may be installed using the standard Elasticsearch installation
procedure.

```bash
elasticsearch-plugin install file://path-to-plugin-zip-file
systemctl restart elasticesearch
```

Replace path-to-plugin-zip-file with the correct path to the plugin installation zip
file.

## Querying
Just like the old plugin, the new plugin may be tested by submitting to an Elasticsearch
server a JSON formatted query of the following form.
```bash
curl -X POST "localhost:9200/patients/_search?pretty=true" -H
'Content-Type: application/json' -d'{
  "query": {
    "function_score": {
      "query": {
        "match_all": {}
      },
      "functions": [
        {
          "script_score": {
            "script": {
              "source": "string_similarity",
              "lang" : "similarity_scripts",
              "params": {
                "matchers": [{
                  "field": "given",
                  "value": "Alis",
                  "matcher": "jaro-winkler",
                  "high": 0.9,
                  "low": 0.1
                }]
              }
            }
          }
        }
      ]
    }
  }
}'
```

The matchers key contains an array of all fields to be searched, configured with the
appropriate field name, value, algorithm and high and low values.

Parameter | Description
---|---
field | The field to be searched e.g. “ given ”.
value | The search term e.g. “ Alis ”.
matcher | The algorithm to use for matching e.g. “ jaro-winkler ”.
high | The score to be assigned to a string that matches the search term perfectly.
low | The score to be assigned to a string that does not match the search term at all.
