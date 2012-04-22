Tatami
================

Presentation
------------------

Tatami is a twitter-like application, for internal use inside a company.

Tatami is made with the following technologies :

- [Apache Cassandra](http://cassandra.apache.org/)
- [The Spring Framework](http://www.springsource.org/)
- HTML5 and [Twitter Bootstrap](http://twitter.github.com/bootstrap/)

Original version of Tatami was developped by [Ippon Technologies](http://www.ippon.fr)

This fork has been enhanced by [DuyHai DOAN](http://doanduyhai.wordpress.com)

Installation
------------

- Install [Maven 3](http://maven.apache.org/)
- Run Cassandra from Maven : mvn cassandra:run
- Run Jetty from Maven : mvn jetty:run
- Connect to the application at http://127.0.0.1:8080/tatami

The default users are "jdubois/password" and "tescolan/password", you can check or modify the
Spring Security configuration at tatami-security.xml

License
-------

Copyright 2012 [DuyHai DOAN](http://doanduyhai.wordpress.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this application except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.