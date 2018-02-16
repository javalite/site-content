<div class="page-header">
   <h1>Releases</h1> 
   
</div>


## Current release version 2.0

The latest current release version is 2.0 and can be downloaded from [Maven Central](http://search.maven.org).

See [Release notes 2.0](release-notes-20) for more information


## Maven config


Here is the Maven  dependency config: 

```xml
<dependency>
    <groupId>org.javalite</groupId>
    <artifactId>activeweb</artifactId>
    <version>2.0</version>
</dependency>
```

Replace `activeweb` with the name of the module you are using (activejdbc, app-config, etc.).


## Current snapshot 2.1-SNAPSHOT

Can be downloaded from the JavaLite Repo: [http://repo.javalite.io/](http://repo.javalite.io/).

If you feel adventurous, you can automatically download snapshots from our repo by adding this config to your pom file:  


```xml
<repositories>
    <repository>
        <id>snapshots1</id>
        <name>JavaLite Snapshots</name>
        <url>http://repo.javalite.io/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
            <checksumPolicy>warn</checksumPolicy>
        </snapshots>
    </repository>
</repositories>
```

  
## Past releases

* [ActiveWeb releases](activeweb_releases)
* [ActiveJDBC releases](activejdbc_releases)

