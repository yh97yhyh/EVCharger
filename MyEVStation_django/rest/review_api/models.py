from django.db import models
from ev_api.models import Stations

# Create your models here.
class Review(models.Model):
    statId = models.ForeignKey(Stations, null=False, on_delete=models.CASCADE)
    userNick = models.CharField(max_length=16, null=False)
    contents = models.CharField(max_length=300, null=False)

    class Meta:
        db_table = "reviews"