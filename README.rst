MongoDB Data Source Plug-in
===========================

.. image:: https://travis-ci.org/curityio/mongodb-datasource.svg?branch=dev
     :target: https://travis-ci.org/curityio/mongodb-datasource
     
.. image:: https://curity.io/assets/images/badges/mongodb-datasource-quality.svg
       :target: https://curity.io/resources/code-examples/status/
       
.. image:: https://curity.io/assets/images/badges/mongodb-datasource-availability.svg
       :target: https://curity.io/resources/code-examples/status/     

This project provides an opens source MongoDB Data Source plug-in for the Curity Identity Server. This allows an administrator to add functionality to Curity which will then enable end users to use MongoDB data source.

System Requirements
~~~~~~~~~~~~~~~~~~~

* Curity Identity Server 3.0.0 and `its system requirements <https://developer.curity.io/docs/latest/system-admin-guide/system-requirements.html>`_

Requirements for Building from Source
"""""""""""""""""""""""""""""""""""""

* Maven 3
* Java JDK v. 8

Compiling the Plug-in from Source
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The source is very easy to compile. To do so from a shell, issue this command: ``mvn package``.

Installation
~~~~~~~~~~~~

To install this plug-in, either download a binary version available from the `releases section of this project's GitHub repository <https://github.com/curityio/mongodb-datasource/releases>`_ or compile it from source (as described above). If you compiled the plug-in from source, the package will be placed in the ``target`` subdirectory. The resulting JAR file or the one downloaded from GitHub needs to placed in the directory ``${IDSVR_HOME}/usr/share/plugins/mongodb``. (The name of the last directory, ``mongodb``, which is the plug-in group, is arbitrary and can be anything.) After doing so, the plug-in will become available as soon as the node is restarted.

.. note::

    The JAR file needs to be deployed to each run-time node and the admin node. For simple test deployments where the admin node is a run-time node, the JAR file only needs to be copied to one location.

For a more detailed explanation of installing plug-ins, refer to the `Curity developer guide <https://developer.curity.io/docs/latest/developer-guide/plugins/index.html#plugin-installation>`_.

Creating a MongoDB Data Source in Curity
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Configuration using the Admin GUI
"""""""""""""""""""""""""""""""""

To configure a new MongoDB data source using the Curity admin UI, do the following after logging in:

1. Click the ``Facilities`` button at the top-right of the screen.
2. Next to ``Data Sources``, click ``New``.

    .. figure:: docs/images/facilities-menu.png
        :align: center
        :width: 600px

3. Enter a name (e.g., ``mongodb1``) and click ``Create``.

    .. figure:: docs/images/create-datasource1.png
        :align: center
        :width: 600px

4. On the next page, Select ``mongodb`` ``Type`` from dropdown.

5. You need to fill in all the required configurations for MongoDB like ``Database``, ``Host`` etc.

    .. figure:: docs/images/create-datasource2.png
        :align: center
        :width: 600px

    .. note::

        The MongoDB-specific configuration is generated dynamically based on the `configuration model defined in the Java interface <https://github.com/curityio/mongodb-datasource/blob/dev/src/main/java/com/curity/mongodb/datasource/config/MongoDataAccessProviderConfiguration.java>`_.


Once all of these changes are made, they will be staged, but not committed (i.e., not running). To make them active, click the ``Commit`` menu option in the ``Changes`` menu. Optionally enter a comment in the ``Deploy Changes`` dialogue and click ``OK``.

Once the configuration is committed and running, the data source can be used like any other.

    .. note::
        This data source is not used for token storage.

Configure MongoDB
~~~~~~~~~~~~~~~~~
You need to create MongoDB indexes in order to apply uniqueness constraint.

To create indexes follow the instructions.

1. Connect to MongoDB (To connect in a shell, Run : ``mongo -u ${username} -p ${password} ${dbName}``)
2. Create unique index for User Account DAP by following command.

    .. code:: python

            db.User.createIndex({userName: 1},{unique: true});

            db.User.createIndex({'emails.value': 1, 'emails.primary': 1}, {'unique': true});

            db.User.createIndex({'phoneNumbers.value': 1, 'phoneNumbers.primary': 1}, {'unique': true});

3. Create unique index for Device DAP by following command.

    .. code:: python

         db.Device.createIndex({'deviceId': 1, 'accountId': 1}, {unique: true})

4. Create unique index for Bucket DAP by following command.

    .. code:: python

       db.Bucket.createIndex({'subject': 1, 'purpose': 1}, {unique: true})

5. Create unique index for Dynamic Clients DAP by following command:

    .. code:: python

        db.DynamicallyRegisteredClients.createIndex({clientId: 1},{unique: true});

After you create above indexes, MongoDB is ready to use.

Required dependencies
~~~~~~~~~~~~~~~~~~~~~
Following dependencies/jars must be in plugin group classpath.

1. `bson-3.6.3.jar <http://central.maven.org/maven2/org/mongodb/bson/3.6.3/bson-3.6.3.jar>`_
2. `mongodb-driver-3.6.3.jar <http://central.maven.org/maven2/org/mongodb/mongo-java-driver/3.6.3/mongo-java-driver-3.6.3.jar>`_
3. `mongodb-driver-core-3.6.3.jar <http://central.maven.org/maven2/org/mongodb/mongodb-driver-core/3.6.3/mongodb-driver-core-3.6.3.jar>`_

License
~~~~~~~

This plugin and its associated documentation is listed under the `Apache 2 license <LICENSE>`_.

More Information
~~~~~~~~~~~~~~~~

Please visit `curity.io <https://curity.io/>`_ for more information about the Curity Identity Server.

Copyright (C) 2018 Curity AB.
