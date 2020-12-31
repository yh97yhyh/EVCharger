from django.db import models

# Create your models here.
class User(models.Model):
    uid = models.CharField(max_length=24, null=False)
    passwd = models.CharField(max_length=24, null=False)
    nickname = models.CharField(max_length=16, null=False)

    class Meta:
        db_table = "users"
       
