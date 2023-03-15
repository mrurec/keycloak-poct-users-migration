update userview uv set issuer = uvb.issuer, subject = uvb.subject
    from userview_backup_20221229_17_44 uvb
where uv.identifier = uvb.identifier;
