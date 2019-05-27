API="https://api.bintray.com"
PROPS=";bt_package=update;bt_version=version;publish=1;override=1;explode=1"
FOLDER="$TRAVIS_BUILD_DIR/de.bund.bfr.knime.fakx.update/target/repository"

echo $TRAVIS_BUILD_DIR

SUBJECT=fauthent
REPO=test

# DEPLOY TO test repo
# =====================================================================================================
# Delete previous version 'version'
curl -u $BINTRAY_USER:$BINTRAY_KEY -X DELETE $API/packages/$SUBJECT/$REPO/update/versions/version

# Create version 'version'
curl -u $BINTRAY_USER:$BINTRAY_KEY -H "Content-Type: application/json" -X POST -d '{"name": "version", "description": ""}' $API/packages/$SUBJECT/$REPO/update/versions

# Upload build
curl -u $BINTRAY_USER:$BINTRAY_KEY -T $TRAVIS_BUILD_DIR/de.bund.bfr.knime.fakx.update/target/de.bund.bfr.knime.fakx.update-1.0.0-SNAPSHOT.zip $API/content/$SUBJECT/$REPO/de.bund.bfr.knime.fakx.update-1.0.0-SNAPSHOT.zip:$PROPS
