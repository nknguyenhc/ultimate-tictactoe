"""A script to combine all codingame files into one file for submission.
"""
import os

def config():
    return {
        "algo": "mcts/parallel",
        "base_dir": "src/main/java",
        "folders": [
            "board",
        ],
        "files": [
            "manager/CodinGame.java",
            "algo/BaseAlgo.java",
        ],
        "output": {
            "prepend": "import java.util.*;\nimport java.io.*;\nimport java.util.concurrent.*;\n\nclass Player {\n",
            "append": "    public static void main(String[] args) {\n        new CodinGame().run();\n    }\n}\n",
        }
    }

def read_file(file_path: str) -> str:
    with open(file_path, 'r') as f:
        line = f.readline()
        # Skip few first lines starting with "package" and "import", or empty lines
        while line.startswith("package") \
                or line.startswith("import") \
                or line.startswith("/**") \
                or line.startswith(" *") \
                or line.startswith(" */") \
                or line.strip() == "":
            line = f.readline()
        
        # Read the rest of the file, assuming first line is the class name, ignoring access modifier
        class_content = line + f.read()
        possible_access_modifier, rest_class_content = class_content.split(" ", maxsplit=1)
        if possible_access_modifier == "public" or possible_access_modifier == "private":
            class_content = rest_class_content
        
        return "static " + class_content

def read_directory(directory_path: str) -> list[str]:
    # Read all files in the directory
    files = os.listdir(directory_path)
    files_content: list[str] = []
    for file in files:
        file_path = os.path.join(directory_path, file)
        if os.path.isfile(file_path):
            files_content.append(read_file(file_path))
    
    return files_content

def main():
    # Get config
    config_data = config()
    base_dir = config_data["base_dir"]

    # Read from all folders in config
    files_content: list[str] = []
    for folder in config_data["folders"]:
        folder_path = os.path.join(base_dir, folder)
        files_content.extend(read_directory(folder_path))
    
    # Read from all files in config
    for file in config_data["files"]:
        file_path = os.path.join(base_dir, file)
        files_content.append(read_file(file_path))
    
    # Read all files from algo folder
    algo_folder_path = os.path.join(base_dir, "algo", config_data["algo"])
    files_content.extend(read_directory(algo_folder_path))

    # Make directory "target/codingame" if not exist
    target_dir = "target/codingame"
    if not os.path.exists(target_dir):
        os.makedirs(target_dir)

    # Write to output file
    output_path = "target/codingame/Player.java"
    with open(output_path, 'w') as f:
        f.write(config_data["output"]["prepend"])
        for content in files_content:
            f.write(content)
        f.write(config_data["output"]["append"])


if __name__ == "__main__":
    main()
