# TruPS bridge

## About
TruPS bridge is a client side piece of software for TruPS project.

In short, it is an executable file (or a Docker container) that lifts up a local web server through which TruPS 
frontend interacts with FHE components.

## Running

| OS      | Docker              | Executable          |
|---------|---------------------|---------------------|
| Linux   | :white_check_mark:  | :white_check_mark:  |
| Windows | :white_check_mark:  | :x:                 |
| MacOS   | :white_check_mark:  | :x:                 |

While Linux users may run the TruPS bridge as an executable, running a Docker container instead of the exectuable.

TruPS bridge runs on port `8282`, therefore in case of running a Docker container it should be exposed.

### Running the executable:
```
# download the archive from releases and untar and unzip it
tar xzf trups-bridge.tar.gz

# make sure trups-bridge has execution permission
chmod +x ./trups-bridge

# run the executable
./trups-bridge
```

### Running the Docker container:
```
# download the Docker image from releases

# import the archive using Docker CLI
docker load -i trups-bridge.tar.gz

# run using Docker CLI, don't forget to expose port 8282
docker run -p 8282:8282 trups-bridge
```

## Building from source
Building an executable from source may be done on Linux only.

To build an executable from source **Python 3.11.9** is required.

Usage of virtual env is **strongly** recommended, as there are some custom steps in the 
dependency installation and build process.

To create and activate the virtual environment run the command:
```
python -m venv .venv

source .venv/bin/activate
```

Dependencies must be installed through the usage of `install_deps.sh` script as external index urls are used for PyTorch,
and additional commands are used to alter some of the dependency code

Install the dependencies:
```
./install_deps.sh
```

To build the executable run `build-executable.sh`:
```
./build-executable.sh
```

If the build process was successful executable shall appear in `dist` directory.

Finally, to build the Docker image run `build-docker-image.sh`:
```
./build-docker-image.sh
```
