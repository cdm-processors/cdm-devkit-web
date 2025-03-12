FROM codercom/code-server:4.93.1

RUN sudo apt-get update

RUN sudo apt-get upgrade -y

RUN sudo apt-get install -y python3 python3-pip

RUN wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.deb

RUN sudo apt install ./jdk-21_linux-x64_bin.deb -y

RUN rm ./jdk-21_linux-x64_bin.deb

RUN pip install cdm-devkit==0.2.2.post0 --break-system-packages

RUN wget https://github.com/cdm-processors/cdm-devkit/releases/download/0.2.2/vscode-cdm-extension-0.2.2.vsix

RUN code-server --install-extension ./vscode-cdm-extension-0.2.2.vsix

RUN rm ./vscode-cdm-extension-0.2.2.vsix

COPY config.yaml .config/code-server/config.yaml

WORKDIR ./user
