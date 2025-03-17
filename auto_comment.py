import os
import sys
import re
from dotenv import load_dotenv
import subprocess
from azure.ai.inference import ChatCompletionsClient
from azure.ai.inference.models import SystemMessage, UserMessage
from azure.core.credentials import AzureKeyCredential

# Load environment variables
load_dotenv()

GITHUB_TOKEN = os.getenv("GITHUB_TOKEN", "").strip()

if not GITHUB_TOKEN:
    print("Error: GITHUB_TOKEN is not set. Make sure you have a .env file.")
    sys.exit(1)

# Set up Azure AI client
client = ChatCompletionsClient(
    endpoint="https://models.inference.ai.azure.com",
    credential=AzureKeyCredential(GITHUB_TOKEN),
)

def get_modified_lines(file_path):
    """
    Get only the modified lines from the Java file using git diff.
    Returns a list of tuples: (line_number, code_line)
    """
    try:
        diff_output = subprocess.check_output(["git", "diff", "-U0", file_path], universal_newlines=True)
        modified_lines = []
        for line in diff_output.split("\n"):
            if line.startswith("@@"):
                # Extract line number from @@ -old,+new @@
                match = re.search(r"\+(\d+)", line)
                if match:
                    line_num = int(match.group(1))
            elif line.startswith("+") and not line.startswith("+++"):  # Ignore file headers
                modified_lines.append((line_num, line[1:]))  # Remove '+' prefix
                line_num += 1  # Increment line number
        
        return modified_lines
    except subprocess.CalledProcessError:
        print(f"Error getting git diff for {file_path}")
        return []

def generate_comment(added_code):
    """ Sends modified Java code to AI and returns the commented version. """
    prompt = f"""
    You are an expert Java developer. Your task is to add meaningful comments to the provided Java code snippet.
    - Use JavaDoc-style comments for class and method documentation.
    - Use inline comments to explain important logic.
    - Do NOT modify the existing logic.
    - Return only the modified Java code with comments, without any explanations or additional text.
    - Do NOT wrap the output in triple backticks or any other formatting markers.

    Java Code:
    {added_code}
    """

    response = client.complete(
        messages=[SystemMessage("You are a professional Java developer."), UserMessage(prompt)],
        model="gpt-4o",
        temperature=0.5,
        max_tokens=1024,
        top_p=1
    )

    return response.choices[0].message.content.strip()

def insert_comments(file_path):
    """ Inserts AI-generated comments into only the modified lines of the file. """
    modified_lines = get_modified_lines(file_path)

    if not modified_lines:
        print(f"No modifications detected in {file_path}.")
        return

    with open(file_path, "r") as file:
        lines = file.readlines()

    for line_num, code in modified_lines:
        ai_comment = generate_comment(code)
        lines.insert(line_num - 1, f"// {ai_comment}\n")  # Insert comment above the modified line

    with open(file_path, "w") as file:
        file.writelines(lines)

    print(f"Updated {file_path} with AI-generated comments.")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python auto_comment.py <JavaFile.java>")
        sys.exit(1)

    java_file = sys.argv[1]
    if not os.path.exists(java_file):
        print(f"Error: {java_file} not found.")
        sys.exit(1)

    insert_comments(java_file)
