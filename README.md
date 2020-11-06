# Simple Gradle S3 Plugin
[![Install](https://img.shields.io/badge/install-plugin-brown.svg)](https://plugins.gradle.org/plugin/com.github.mgk.gradle.s3)
[![MIT License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](LICENSE)

Simple Gradle plugin that uploads and downloads S3 objects. This is a fork of the [mgk/s3-plugin](https://github.com/mgk/s3-plugin), which no longer appears to be under active development.
It accepts directly the aws keys and secret to access the s3

## Setup

Add the following to your build.gradle file:

```groovy
plugins {
  id 'com.vduttadev.core.gradle.s3' version '1.0.0'
}
```

## Versioning

This project uses [semantic versioning](http://semver.org)

See [gradle plugin page](https://plugins.gradle.org/plugin/com.mgd.core.gradle.s3) for other versions.

# Usage

## Authentication

The S3 plugin accepts the aws credentails. Additionally you can specify a credentials by setting the project `s3.accessKey, s3.accessSecret, s3.region` property:

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


### S3Upload

Uploads one or more files to S3. This task has two modes of operation: single file upload and directory upload (including recursive upload of all child subdirectories). Properties that apply to both modes:

  + `bucket` - S3 bucket to use *(optional, defaults to the project `s3` configured bucket)*

For a single file upload:

  + `key` - key of S3 object to create
  + `file` - path of file to be uploaded
  + `overwrite` - *(optional, default is `false`)*, if `true` the S3 object is created or overwritten if it already exists.

By default `S3Upload` does not overwrite the S3 object if it already exists. Set `overwrite` to `true` to upload the file even if it exists.

For a directory upload:

  + `keyPrefix` - root S3 prefix under which to create the uploaded contents
  + `sourceDir` - local directory containing the contents to be uploaded

A directory upload will always overwrite existing content if it already exists under the specified S3 prefix.

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
