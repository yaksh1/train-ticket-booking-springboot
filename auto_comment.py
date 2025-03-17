import os
import sys
import subprocess
import re
from dotenv import load_dotenv
from azure.ai.inference import ChatCompletionsClient
from azure.ai.inference.models import SystemMessage, UserMessage
from azure.core.credentials import AzureKeyCredential

# Load environment variables
load_dotenv()
GITHUB_TOKEN = os.getenv("GITHUB_TOKEN", "").strip()

if not GITHUB_TOKEN:
    print("Error: GITHUB_TOKEN is not set.")
    sys.exit(1)

# Set up Azure AI client
client = ChatCompletionsClient(
    endpoint="https://models.inference.ai.azure.com",
    credential=AzureKeyCredential(GITHUB_TOKEN),
)

def get_modified_lines(file_path):
    """ Extracts changed lines from the given Java file using git diff. """
    try:
        diff_output = subprocess.run(
            ["git", "diff", "-U0", file_path],
            capture_output=True, text=True
        ).stdout

        modified_lines = []
        for line in diff_output.split("\n"):
            if line.startswith("+") and not line.startswith("++"):
                modified_lines.append(line[1:])  # Remove leading '+'

        return "\n".join(modified_lines).strip() if modified_lines else None
    except Exception as e:
        print(f"Error fetching modified lines: {e}")
        return None

def generate_commented_code(changed_code):
    """ Requests AI to generate comments only for the modified code. """
    prompt = f"""
    You are an expert Java developer. Your task is to add meaningful comments to the provided Java code. 
    - Use JavaDoc-style comments for class and method documentation.
    - Use inline comments to explain important logic.
    - Do NOT modify the existing logic.
    - Return only the modified Java code with comments, without any explanations or additional text.
    - Do NOT wrap the output in triple backticks or any other formatting markers.


    Java Code:
    {changed_code}
    """

    response = client.complete(
        messages=[
            SystemMessage("You are an expert Java developer."),
            UserMessage(prompt),
        ],
        model="gpt-4o",
        temperature=0.5,
        max_tokens=2048,
        top_p=1
    )

    return response.choices[0].message.content.strip()

def update_java_file(file_path, commented_code):
    """ Replaces the modified lines with AI-commented versions in the original file. """
    with open(file_path, "r") as file:
        original_lines = file.readlines()

    modified_lines = commented_code.split("\n")
    
    # Replace only modified lines in the original file
    updated_lines = []
    i = 0
    for line in original_lines:
        if line.strip() in modified_lines:
            updated_lines.append(modified_lines[i] + "\n")
            i += 1
        else:
            updated_lines.append(line)

    with open(file_path, "w") as file:
        file.writelines(updated_lines)

    print(f"Updated {file_path} with AI-generated comments.")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python auto_comment.py <JavaFile.java>")
        sys.exit(1)

    java_file = sys.argv[1]
    if not os.path.exists(java_file):
        print(f"Error: {java_file} not found.")
        sys.exit(1)

    modified_code = get_modified_lines(java_file)
    if modified_code:
        commented_code = generate_commented_code(modified_code)
        update_java_file(java_file, commented_code)
    else:
        print(f"No modifications detected in {java_file}.")
