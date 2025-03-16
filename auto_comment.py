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
    print("Error: GITHUB_TOKEN is not set. Ensure you have a .env file.")
    sys.exit(1)

# Set up Azure AI client
client = ChatCompletionsClient(
    endpoint="https://models.inference.ai.azure.com",
    credential=AzureKeyCredential(GITHUB_TOKEN),
)

def get_modified_java_files():
    """Fetches only modified (but not committed) Java files."""
    result = subprocess.run(["git", "diff", "--name-only"], capture_output=True, text=True)
    files = result.stdout.split("\n")
    return [f for f in files if f.endswith(".java") and os.path.exists(f)]

def generate_commented_code(java_code):
    """Sends Java code to AI and retrieves only the commented version."""
    prompt = f"""
    You are an expert Java developer. Your task is to add meaningful comments to the given Java code. 
    - Use JavaDoc-style comments for class and method documentation.
    - Use inline comments to explain important logic.
    - Do NOT modify or rewrite the logic of the code.
    - Do NOT add explanations, summaries, or any extra text outside comments.
    - Return ONLY the modified Java code with comments, without extra formatting.

    Java Code:
    {java_code}
    """

    response = client.complete(
        messages=[
            SystemMessage("You are an expert Java developer who writes professional and meaningful comments."),
            UserMessage(prompt),
        ],
        model="gpt-4o",
        temperature=0.5,
        max_tokens=4096,
        top_p=1
    )

    if not response.choices or not response.choices[0].message.content:
        print("Error: AI response is empty.")
        return java_code  # Return original if AI fails

    commented_code = response.choices[0].message.content.strip()

    # Clean AI formatting issues
    commented_code = re.sub(r"^```java\s*", "", commented_code)
    commented_code = re.sub(r"\s*```$", "", commented_code)

    # Remove any unintended AI explanations
    unwanted_patterns = [
        "### Explanation", "This rewritten version", "Enhancements:", "Changes made"
    ]
    for pattern in unwanted_patterns:
        if pattern in commented_code:
            commented_code = commented_code.split(pattern, 1)[0].strip()

    return commented_code

def process_java_file(file_path):
    """Reads a Java file, processes it with AI, and writes the updated version back."""
    with open(file_path, "r", encoding="utf-8") as file:
        java_code = file.read()

    print(f"Processing {file_path}...")

    commented_code = generate_commented_code(java_code)

    if commented_code and commented_code != java_code:
        with open(file_path, "w", encoding="utf-8") as file:
            file.write(commented_code)
        print(f"Updated {file_path} with AI-generated comments.")
    else:
        print(f"No changes were made to {file_path}.")

if __name__ == "__main__":
    modified_files = get_modified_java_files()

    if not modified_files:
        print("No modified Java files detected.")
        sys.exit(0)

    for file in modified_files:
        process_java_file(file)

    # Add modified files back to Git
    subprocess.run(["git", "add"] + modified_files)
    subprocess.run(["git", "commit", "-m", "Added AI-generated comments"])
