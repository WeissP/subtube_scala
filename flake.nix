{
  inputs = {
    devshell.url = "github:numtide/devshell";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, flake-utils, devshell, nixpkgs, ... }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          overlays = [ devshell.overlays.default ];
        };
        jdk = pkgs.jdk19;
        basic = {
          imports = map pkgs.devshell.importTOML [
            ./env_config/server.toml
            ./env_config/db.toml
          ];
          name = "subtube";
          packages = [ jdk (pkgs.metals.override { jre = jdk; }) ];
          commands = [{ package = pkgs.mill.override { jre = jdk; }; }];
          env = [{
            name = "JAVA_HOME";
            value = "${jdk}";
          }];
        };
        extraImports = files:
          basic // {
            imports = basic.imports ++ map pkgs.devshell.importTOML files;
          };
      in {
        devShells = {
          default = pkgs.devshell.mkShell basic;
          backend =
            pkgs.devshell.mkShell (extraImports [ ./env_config/backend.toml ]);
          frontend =
            pkgs.devshell.mkShell (extraImports [ ./env_config/frontend.toml ]);
          ci = pkgs.devshell.mkShell (extraImports [ ./env_config/ci.toml ]);
        };
      });
}
