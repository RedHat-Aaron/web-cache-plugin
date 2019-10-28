web-cache-plugin
You can use this plugin by follow way.

Step 1
  <plugins>
            <plugin>
                <!--打包时重命名插件-->
                <groupId>com.athena</groupId>
                <artifactId>web-cache-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <configuration>
                    <baseDir>${project.basedir}</baseDir>
                </configuration>
                <executions>
                    <execution>
                        <id>webCacheRefresh</id>
                        <phase>prepare-package</phase>
                        <goals>
                            <goal>webCacheRefresh</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>webCacheRevert</id>
                        <phase>package</phase>
                        <goals>
                            <goal>webCacheRevert</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
   </plugins>
   
   Step 2
   You need package project by commond of maven.
   maven clean install -Dmaven.test.skip=true
