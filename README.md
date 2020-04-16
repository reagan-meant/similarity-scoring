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
                "score_mode": "bayes",
                "matchers": [{
                  "field": "given",
                  "value": "Alis",
                  "matcher": "jaro-winkler-similarity",
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
To combine scores using Fellegi-Sunter you need to have m and u values for the fields as well
as a baseScore parameter because ElasticSearch doesn't allow negative scores.  The baseScore
should be the minimum for the min_score on the query but it should be adjusted higher based
on your selection criteria.
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
                "score_mode": "fellegi-sunter",
                "base_score": 100.0
                "matchers": [{
                  "field": "given",
                  "value": "Alis",
                  "matcher": "jaro-winkler-similarity",
                  "threshold": 0.9,
                  "m_value": 0.95736,
                  "u_value": 0.0003415
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
appropriate field name, value, algorithm, score_mode and additional parameters based on the score_mode.

Parameter | Description
---|---
field | The field to be searched e.g. "given".
value | The search term e.g. "Alis".
matcher | The algorithm to use for matching e.g. "jaro-winkler-similarity".
score_mode | How to combine scores for multiple matchers/fields.  The options are:  fellegi-sunter, bayes, multiply, or add.
high | The score to be assigned to a string that matches the search term perfectly.  Applies to the bayes score_mode.
low | The score to be assigned to a string that does not match the search term at all.  Applies to the bayes score_mode.
threshold | The threshold for the field being a match for the fellegi-sunter score_mode.
m_value | The *m* value for the field for the fellegi-sunter score_mode.
u_value | The *u* value for the field for the fellegi-sunter score_mode.
