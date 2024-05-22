{ pkgs }: {
    deps = [
        pkgs.openjdk
        pkgs.maven
    ];

    preBuild = ''
        mvn install:install-file -DgroupId=org.json -DartifactId=json -Dversion=20230227 -Dpackaging=jar -Dfile=https://repo1.maven.org/maven2/org/json/json/20230227/json-20230227.jar
    '';
}
