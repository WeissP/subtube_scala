{
  inputs = {
    typelevel-nix.url = "github:typelevel/typelevel-nix";
    nixpkgs.follows = "typelevel-nix/nixpkgs";
    flake-utils.follows = "typelevel-nix/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils, typelevel-nix }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          overlays = [ typelevel-nix.overlay ];
        };
        basic = {
          imports = [ typelevel-nix.typelevelShell ]
            ++ map pkgs.devshell.importTOML [ ./env_config/server.toml ];
          name = "substube";
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
