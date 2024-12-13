MongoDB Data Source Plug-in
===========================

.. image:: https://travis-ci.org/curityio/mongodb-datasource.svg?branch=dev
     :target: https://travis-ci.org/curityio/mongodb-datasource
     
.. image:: https://img.shields.io/badge/quality-demo-red
    :target: https://curity.io/resources/code-examples/status/

.. image:: https://img.shields.io/badge/availability-source-blue
    :target: https://curity.io/resources/code-examples/status/
   

This project provides an opens source MongoDB Data Source plug-in for the Curity Identity Server. This allows an administrator to add functionality to Curity which will then enable end users to use MongoDB data source.

System Requirements
~~~~~~~~~~~~~~~~~~~

* Curity Identity Server and `its system requirements <https://developer.curity.io/docs/latest/system-admin-guide/system-requirements.html>`_

Requirements for Building from Source
"""""""""""""""""""""""""""""""""""""

* Maven 3
* Java JDK v. 21

Compiling the Plug-in from Source
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The source is very easy to compile. To do so from a shell, issue this command: ``mvn package``.

Installation
~~~~~~~~~~~~

To install this plug-in, compile it from source (as described above). The resulting JAR files in ``target/libs`` needs to placed in the directory ``${IDSVR_HOME}/usr/share/plugins/mongodb``. (The name of the last directory, ``mongodb``, which is the plug-in group, is arbitrary and can be anything.) After doing so, the plug-in will become available as soon as the node is restarted.

.. note::

    The JAR files needs to be deployed to each run-time node and the admin node. For simple test deployments where the admin node is a run-time node, the JAR files only needs to be copied to one location.

For a more detailed explanation of installing plug-ins, refer to the `Curity developer guide <https://developer.curity.io/docs/latest/developer-guide/plugins/index.html#plugin-installation>`_.

Creating a MongoDB Data Source in Curity
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Configuration using the Admin GUI
"""""""""""""""""""""""""""""""""

To configure a new MongoDB data source using the Curity admin UI, do the following after logging in:

1. Click the ``Facilities`` button at the top-right of the screen.
2. Next to ``Data Sources``, click ``+``.

    .. figure:: docs/images/facilities-menu.jpg
        :align: center
        :width: 600px

3. Enter a name (e.g., ``mongodb1``) and select ``mongodb`` ``Type`` then click ``Create``.

    .. figure:: docs/images/create-datasource1.jpg
        :align: center
        :width: 600px

5. You need to fill in all the required configurations for MongoDB like ``Database``, ``Host`` etc.

    .. figure:: docs/images/create-datasource2.jpg
        :align: center
        :width: 600px

    .. note::

        The MongoDB-specific configuration is generated dynamically based on the `configuration model defined in the Java interface <https://github.com/curityio/mongodb-datasource/blob/dev/src/main/java/io/curity/mongodb/datasource/config/MongoDataAccessProviderConfiguration.java>`_.


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

1. `bson-5.2.1.jar <https://repo1.maven.org/maven2/org/mongodb/bson/5.2.1/bson-5.2.1.jar>`_
2. `mongodb-driver-5.1.1.jar <https://repo1.maven.org/maven2/org/mongodb/bson/5.2.1/bson-5.2.1.jar>`_
3. `mongodb-driver-core-5.2.1.jar <https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-core/5.2.1/mongodb-driver-core-5.2.1.jar>`_
4. `mongodb-driver-sync-5.2.1.jar <https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-sync/5.2.1/mongodb-driver-sync-5.2.1.jar>`_

License
~~~~~~~

This plugin and its associated documentation is listed under the `Apache 2 license <LICENSE>`_.

More Information
~~~~~~~~~~~~~~~~

Please visit `curity.io <https://curity.io/>`_ for more information about the Curity Identity Server.

Copyright (C) 2018 Curity AB.
