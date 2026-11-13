cd "$(dirname "$0")"
if [[ -e 'src' ]]; then
  echo "ERROR: src/ already exists. Maybe you already copied it?"
  exit 1
fi
LANG=$(git branch --show-current | cut -d- -f2)
git fetch origin "lab4.2-$LANG"
git restore --source="origin/lab4.2-$LANG" -- src
echo "successfully downloaded lab4.2 starter code for $LANG to src/"
