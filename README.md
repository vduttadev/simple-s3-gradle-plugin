# Simple Gradle S3 Plugin
[![MIT License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](LICENSE)

Simple Gradle plugin that uploads and downloads S3 objects. This is a fork of the [mgk/s3-plugin](https://github.com/mgk/s3-plugin), which no longer appears to be under active development.
It accepts directly the aws keys and secret to access the s3

## Setup

Add the following to your build.gradle file:

```groovy
plugins {
  id 'com.vdnsmd.core.gradle.s3' version '1.0.0'
}
```

## Versioning

See [gradle plugin page](https://plugins.gradle.org/plugin/com.mgd.core.gradle.s3) for other versions.

# Usage

## Authentication

The S3 plugin accepts the aws credentials. Additionally you can specify a credentials by setting the project `s3.accessKey, s3.accessSecret, s3.region` property:

```groovy
s3 {
    accessKey = 'accessKey'
    accessSecret = 'accessSecret'
    region = 'region'
}
```
## Default S3 Bucket

The `s3.bucket` property sets a default S3 bucket that is common to all tasks. This can be useful if all S3 tasks operate against the same Amazon S3 bucket.

```groovy
s3 {
    bucket = 'my.default.bucketname'
}
```

## Tasks

The following Gradle tasks are provided.


### S3Download

Downloads one or more S3 objects. This task has two modes of operation: single file
download and recursive download. Properties that apply to both modes:

  + `bucket` - S3 bucket to use *(optional, defaults to the project `s3` configured bucket)*

For a single file download:

  + `key` - key of S3 object to download
  + `file` - local path of file to save the download to

For a recursive download:

  + `keyPrefix` - S3 prefix of objects to download
  + `destDir` - local directory to download objects to

***Note***:  

Recursive downloads create a sparse directory tree containing the full `keyPrefix` under `destDir`. So with an S3 bucket
containing the object keys:

```
top/foo/bar
top/README
```

a recursive download:

```groovy
task downloadRecursive(type: S3Download) {
  keyPrefix = 'folder/'
  destDir = 'local-destination'
}
```

results in this local tree:

```
local-destination/
└── folder/
```

So all the files under `$bucket$/folder` are downloaded.

For example:

```groovy
def localTree = 'path/to/some/location'

task downloadRecursive(type: S3Download) {
    bucket = 's3-bucket-name'
    keyPrefix = "${localDir}"
    destDir = "${buildDir}/download"
}
```

## Progress Reporting

Downloads report percentage progress at the gradle INFO level. Run gradle with the `-i` option to see download progress.

## License
[![MIT License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](LICENSE)
